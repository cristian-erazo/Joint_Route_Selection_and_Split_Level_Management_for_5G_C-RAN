graph [
  node [
    id 0
    label "0"
    type 2
    prc 2
  ]
  node [
    id 1
    label "1"
    type 2
    prc 5
  ]
  node [
    id 2
    label "2"
    type 2
    prc 3
  ]
  node [
    id 3
    label "3"
    type 1
    prc 4
    ant 3
    prb 2
    x 72
    y 51
  ]
  node [
    id 4
    label "4"
    type 1
    prc 2
    ant 9
    prb 5
    x 32
    y 53
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 2
    prb 5
    x 91
    y 78
  ]
  node [
    id 6
    label "6"
    type 1
    prc 5
    ant 3
    prb 2
    x 23
    y 94
  ]
  edge [
    source 0
    target 5
    bandwith 394
    delay 344
  ]
  edge [
    source 1
    target 4
    bandwith 742
    delay 280
  ]
  edge [
    source 2
    target 3
    bandwith 244
    delay 228
  ]
  edge [
    source 2
    target 6
    bandwith 204
    delay 403
  ]
]
