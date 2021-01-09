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
    type 1
    prc 3
    ant 3
    prb 2
    x 24
    y 20
  ]
  node [
    id 2
    label "2"
    type 1
    prc 2
    ant 4
    prb 5
    x 21
    y 100
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 7
    prb 2
    x 81
    y 90
  ]
  edge [
    source 0
    target 2
    bandwith 513
    delay 470
  ]
  edge [
    source 0
    target 1
    bandwith 703
    delay 169
  ]
  edge [
    source 0
    target 3
    bandwith 107
    delay 106
  ]
]
