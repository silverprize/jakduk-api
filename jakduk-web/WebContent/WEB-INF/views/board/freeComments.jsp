<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>    
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>    
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<!DOCTYPE html>
<html ng-app="jakdukApp">
<head>
	<title><spring:message code="board.name.free"/> &middot; <spring:message code="common.jakduk"/></title>
	
	<link rel="stylesheet" href="<%=request.getContextPath()%>/resources/unify/assets/css/pages/page_search_inner.css">
	
	<jsp:include page="../include/html-header.jsp"></jsp:include>
</head>
<body>

<sec:authorize access="isAnonymous()">
	<c:set var="authRole" value="ANNONYMOUS"/>
</sec:authorize>
<sec:authorize access="hasAnyRole('ROLE_USER_01', 'ROLE_USER_02', 'ROLE_USER_03')">
	<c:set var="authRole" value="USER"/>
</sec:authorize>
	
<div class="wrapper">
	<jsp:include page="../include/navigation-header.jsp"/>
	
	<!--=== Breadcrumbs ===-->
	<div class="breadcrumbs">
		<div class="container">
			<h1 class="pull-left"><a href="<c:url value="/board/free/refresh"/>"><spring:message code="board.name.free"/></a></h1>
				<ul class="pull-right breadcrumb">
			      <li><a href="<c:url value="/board/free/posts"/>"><spring:message code="board.free.breadcrumbs.posts"/></a></li>
			      <li class="active"><spring:message code="board.free.breadcrumbs.comments"/></li>
      			</ul>						
		</div><!--/container-->
	</div><!--/breadcrumbs-->
	<!--=== End Breadcrumbs ===-->	
	
	<!--=== Content Part ===-->
	<div class="container content" ng-controller="boardCtrl">
	
	<div class="row">
		<!--Top Likes Rows-->
		<div class="col-md-6">
               <h2 class="heading-sm">
                   <i class="icon-custom rounded-x icon-sm icon-color-u fa fa-thumbs-o-up"></i>
                   <span><spring:message code="board.top.likes"/></span>
               </h2>
		       <table class="table table-hover">
					<tbody>
						<tr ng-repeat="post in topLike">
							<td class="text-overflow max-width-240">
								<a ng-href='<c:url value="/board/free/{{post.seq}}"/>'>
									<strong ng-if="post.status.delete == 'delete'"><spring:message code="board.msg.deleted"/></strong>
									<strong ng-if="post.status.delete != 'delete'">{{post.subject}}</strong>
								</a>	
							</td>
							<td>
								<span class="text-primary">
									<i class="fa fa-thumbs-o-up"></i>
									<strong>{{post.count}}</strong>
								</span>	
							</td>
						</tr>                                
					</tbody>
				</table>                
           </div>	
           <!--End Top Likes Rows-->	
           <!--Top Comments Rows-->
		<div class="col-md-6">
               <h2 class="heading-sm">
                   <i class="icon-custom rounded-x icon-sm icon-color-u fa fa-comment-o"></i>
                   <span><spring:message code="board.top.comments"/></span>
               </h2>
	  			<table class="table table-hover">
	      			<tbody>
						<tr ng-repeat="post in topComment">
							<td class="text-overflow max-width-240">
								<a ng-href='<c:url value="/board/free/{{post.seq}}"/>'>
									<strong ng-if="post.status.delete == 'delete'"><spring:message code="board.msg.deleted"/></strong>
									<strong ng-if="post.status.delete != 'delete'">{{post.subject}}</strong>
								</a>	
							</td>
							<td>
								<span class="text-default">
									<i class="fa fa-comment-o"></i>
									<strong>{{post.count}}</strong>
								</span>	
							</td>
						</tr>  
					</tbody>
				</table>               
           </div>	           
		<!--End Top Comments Rows-->
	</div>		
	
	<div class="row">
		<div class="col-sm-6 margin-bottom-10">
			<div class="btn-group">
			<c:choose>
				<c:when test="${authRole == 'ANNONYMOUS'}">
				<button type="button" class="btn-u btn-brd rounded" onclick="needLogin();">
					<span aria-hidden="true" class="icon-pencil"></span>
				</button>	
				</c:when>
				<c:when test="${authRole == 'USER'}">
				<button type="button" class="btn-u btn-brd rounded" onclick="location.href='<c:url value="/board/free/write"/>'">
					<span aria-hidden="true" class="icon-pencil"></span>
				</button>	
				</c:when>	
			</c:choose>
			</div>
		</div>
		<div class="col-sm-6 sm-margin-bottom-10">
	       <div class="input-group">
	           <input type="text" class="form-control" ng-model="searchWords" ng-init="searchWords=''" 
	           ng-keypress="($event.which === 13)?btnEnter():return" 
	           placeholder='<spring:message code="search.placeholder.words"/>'>
	           <span class="input-group-btn">
	               <button class="btn-u" type="button" ng-click="btnEnter();"><i class="fa fa-search"></i></button>
	           </span>
	       </div>
	       <span class="text-danger" ng-show="enterAlert">{{enterAlert}}</span>
		</div>			
	</div>	
	
	<hr/>
	
		<!-- search results of post -->
		<div class="margin-bottom-10">
			<div>
				<ul class="list-unstyled blog-latest-posts">
					<li ng-repeat="comment in comments">
						<p><i aria-hidden="true" class="icon-user"></i> {{comment.writer.username}}&nbsp;&nbsp;{{dateFromObjectId(comment.id) | date:dateTimeFormat.dateTime}}</p>
						<p><a href='<c:url value="/board/free/{{comment.boardItem.seq}}"/>' ng-bind-html="comment.content"></a></p>
						<p class="text-overflow">							
							<a class=text-gray href='<c:url value="/board/free/{{comment.boardItem.seq}}"/>'>
								<spring:message code="board.subject"/>
								: 
								{{postsHavingComments[comment.boardItem.id].subject}}
							</a>
						</p>
					</li>
				</ul>			
			</div>
	    </div>		
	    
	<hr/>	    
	    
	<c:choose>
		<c:when test="${authRole == 'ANNONYMOUS'}">
		<button type="button" class="btn-u btn-brd rounded" onclick="needLogin();">
			<span aria-hidden="true" class="icon-pencil"></span>
		</button>	
		</c:when>
		<c:when test="${authRole == 'USER'}">
		<button type="button" class="btn-u btn-brd rounded" onclick="location.href='<c:url value="/board/free/write"/>'">
			<span aria-hidden="true" class="icon-pencil"></span>
		</button>	
		</c:when>	
	</c:choose>	    
	    
		<div class="text-left">
        	<pagination ng-model="currentPage" total-items="totalItems" max-size="10" items-per-page="itemsPerPage"
        	previous-text="&lsaquo;" next-text="&rsaquo;" ng-change="pageChanged()"></pagination>
		</div>	    
	
	<!--=== End Content Part ===-->
	</div>
	
	<jsp:include page="../include/footer.jsp"/>
</div> <!-- End wrapper -->

<script src="<%=request.getContextPath()%>/resources/jquery/dist/jquery.min.js"></script>
<script src="<%=request.getContextPath()%>/resources/angular-sanitize/angular-sanitize.min.js"></script>
<script src="<%=request.getContextPath()%>/resources/angular-bootstrap/ui-bootstrap-tpls.min.js"></script>
<script src="<%=request.getContextPath()%>/resources/jakduk/js/jakduk.js"></script>
<script type="text/javascript">
var jakdukApp = angular.module('jakdukApp', ['ngSanitize', 'ui.bootstrap']);

jakdukApp.controller("boardCtrl", function($scope, $http) {
	$scope.dataCommentsConn = "none";
	$scope.dataTopPostConn = "none";
	$scope.comments = [];
	$scope.topLike = [];
	$scope.topComment = [];
	$scope.postsHavingComments = {};
	$scope.dateTimeFormat = JSON.parse('${dateTimeFormat}');
	$scope.itemsPerPage = Jakduk.ItemsPerPageOnBoardComments;

	angular.element(document).ready(function() {
		var page = parseInt("${boardListInfo.page}");
		var size = parseInt("${boardListInfo.size}");
		var total = Number("${totalComments}");
		
		if (page > 0) {
			$scope.currentPage = page;
		} else {
			$scope.currentPage = 1;			
		}
		
		$scope.totalItems = total;
		$scope.itemsPerPage = size;
		
		$scope.getDataCommentsList(page, size);
		$scope.getDataBestLike();
		
		App.init();
	});
	
	$scope.objectIdFromDate = function(date) {
		return Math.floor(date.getTime() / 1000).toString(16) + "0000000000000000";
	};
	
	$scope.dateFromObjectId = function(objectId) {
		return new Date(parseInt(objectId.substring(0, 8), 16) * 1000);
	};
	
	$scope.intFromObjectId = function(objectId) {
		return parseInt(objectId.substring(0, 8), 16) * 1000;
	};
	
	$scope.getDataCommentsList = function(page, size) {
		var bUrl = '<c:url value="/board/data/free/comments.json?page=' + page + '&size=' + size + '"/>';
		
		if ($scope.dataCommentsConn == "none") {
			
			var reqPromise = $http.get(bUrl);
			
			$scope.dataCommentsConn = "loading";
			
			reqPromise.success(function(data, status, headers, config) {
				//console.log(data);
				
				if (data.comments != null) {
					$scope.comments = data.comments;
				}
				
				if (data.postsHavingComments != null) {
					$scope.postsHavingComments = data.postsHavingComments;
				}
				
				$scope.dataCommentsConn = "none";
			});
			reqPromise.error(function(data, status, headers, config) {
				$scope.dataCommentsConn = "none";
				$scope.error = '<spring:message code="common.msg.error.network.unstable"/>';
			});
		}
	};
	
	$scope.getDataBestLike = function() {
		var bUrl = '<c:url value="/board/data/free/top.json"/>';
		
		if ($scope.dataTopPostConn == "none") {
			
			var reqPromise = $http.get(bUrl);
			
			$scope.dataTopPostConn = "loading";
			
			reqPromise.success(function(data, status, headers, config) {
				$scope.topLike = data.topLike;
				$scope.topComment = data.topComment;
				
				$scope.dataTopPostConn = "none";
			});
			reqPromise.error(function(data, status, headers, config) {
				$scope.dataTopPostConn = "none";
				$scope.error = '<spring:message code="common.msg.error.network.unstable"/>';
			});
		}
	};
	
	$scope.btnEnter = function() {
		
		var isValid = true;
		
		if ($scope.searchWords.trim() < 1) {
			$scope.enterAlert = '<spring:message code="search.msg.enter.words.you.want.search.words"/>';
			isValid = false;
		}
		
		if (isValid) {
			location.href = '<c:url value="/search?q=' + $scope.searchWords.trim() + '&w=CO;"/>';	
		}
	};	
	
	$scope.pageChanged = function() {
		var page = $scope.currentPage;
		
		location.href = '<c:url value="/board/free/comments?page=' + page + '"/>';
	};	

});

function needLogin() {
	if (confirm('<spring:message code="board.msg.need.login.for.write"/>') == true) {
		location.href = '<c:url value="/board/free/write"/>';
	}
}
</script>

<jsp:include page="../include/body-footer.jsp"/>

</body>
</html>